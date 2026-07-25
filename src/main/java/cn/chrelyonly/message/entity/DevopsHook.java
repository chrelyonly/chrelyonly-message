package cn.chrelyonly.message.entity;

import cn.chrelyonly.message.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@TableName("strawberry_devops_hook")
@EqualsAndHashCode(callSuper = true)
public class DevopsHook extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 标题
     */
    private String title;
    /**
     * 流水线hook
     */
    private String url;
    /**
     * 仓库名
     */
    private String repoName;

}
