package com.office.yancao.untils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TobaccoTimeoutChecker {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(TobaccoTimeoutChecker.class);

    // 数据库配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/yancao?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "zjz1012.";
    private static final String TABLE_NAME = "tobacco_record"; // 假设表名为tobacco_record

    public static void main(String[] args) {
        // 初始化定时任务：启动后立即执行一次，之后每天执行一次
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                TobaccoTimeoutChecker::checkAndUpdateTimeout,
                0, // 初始延迟0秒
                1, // 间隔1天
                TimeUnit.DAYS
        );
        logger.info("烟叶超时检查服务已启动，将每天自动执行检查...");
    }

    /**
     * 检查并更新超时状态
     */
    public static void checkAndUpdateTimeout() {
        Connection conn = null;
        PreparedStatement queryStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            // 1. 获取数据库连接
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // 开启事务

            // 2. 查询符合条件的记录：add_work_order_id为空，exit_work_order_id不为空，且is_timeout为0（未超时）
            String querySql = String.format(
                    "SELECT id, brand, operate_time " +
                    "FROM %s " +
                    "WHERE add_work_order_id IS NULL " +
                    "  AND exit_work_order_id IS NOT NULL " +
                    "  AND is_timeout = 0", TABLE_NAME);
            queryStmt = conn.prepareStatement(querySql);
            rs = queryStmt.executeQuery();

            // 3. 遍历记录，判断是否超时
            LocalDateTime now = LocalDateTime.now();
            int updateCount = 0;

            while (rs.next()) {
                Long recordId = rs.getLong("id");
                String brand = rs.getString("brand");
                LocalDateTime operateTime = rs.getTimestamp("operate_time").toLocalDateTime();

                // 计算operate_time与当前时间的天数差
                long daysDiff = ChronoUnit.DAYS.between(operateTime, now);

                // 若超过10天，且brand不包含"利群"，则更新is_timeout为1
                if (daysDiff > 10) {
                    
                        String updateSql = String.format(
                                "UPDATE %s " +
                                "SET is_timeout = 1 " +
                                "WHERE id = ?", TABLE_NAME);
                        updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setLong(1, recordId);
                        updateStmt.executeUpdate();
                        updateCount++;
                        logger.info("记录ID: {} 已超时（{}天），已更新is_timeout为1", recordId, daysDiff);
                    
                }
            }

            conn.commit(); // 提交事务
            logger.info("本次检查完成，共更新 {} 条超时记录", updateCount);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 事务回滚
                } catch (SQLException ex) {
                    logger.error("事务回滚失败", ex);
                }
            }
            logger.error("检查超时记录时发生异常", e);
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (queryStmt != null) queryStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("关闭数据库资源失败", e);
            }
        }
    }
}