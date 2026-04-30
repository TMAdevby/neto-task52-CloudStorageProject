
package com.example.netotask52cloudstorageproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor

public class FileInfo {

    private String filename;
    private Long size;

    public FileInfo(String filename, Long size) {
        this.filename = filename;
        this.size = size;
    }

    public static FileInfo fromEntity(com.example.netotask52cloudstorageproject.model.StoredFile file) {
        return new FileInfo(file.getFilename(), file.getSize());
    }
}
