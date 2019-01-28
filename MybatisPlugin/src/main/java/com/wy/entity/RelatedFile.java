package com.wy.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name="ti_related_file")
public class RelatedFile implements Serializable {
    @Id
    @Column(nullable=false)
    @GeneratedValue(generator ="JDBC")
    private Integer fileId;

    //存储在本地的名称,规则是yyyyMMdd_文件后缀_32uuid
    @Column(nullable=false)
    private String localName;

    //文件本来的名字
    @Column(nullable=false)
    private String fileName;

    //文件类型1图片2音频3视频4文本5其他
    @Column(nullable=false)
    private Byte fileType;

    //文件大小,单位M
    @Column
    private BigDecimal fileSize;

    //音视频文件时长,格式为HH:mm:ss
    @Column
    private String fileTime;

    //文件后缀,不需要点
    @Column
    private String fileSuffix;

    //上传时间
    @Column(nullable=false)
    private Date uploadtime;

    private static final long serialVersionUID = 1L;
}