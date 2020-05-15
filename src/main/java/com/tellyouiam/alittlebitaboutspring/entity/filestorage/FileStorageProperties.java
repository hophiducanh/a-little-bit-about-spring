package com.tellyouiam.alittlebitaboutspring.entity.filestorage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;
}
