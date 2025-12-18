package com.office.yancao.dto.admin;

import com.office.yancao.entity.admin.SixsTaskInstance;
import com.office.yancao.entity.admin.SixsTaskTemplate;
import lombok.Data;

@Data
public class SixsTaskDetailRespDTO {
    private SixsTaskTemplate taskInfo;
    private SixsTaskInstance taskInstance;
}
