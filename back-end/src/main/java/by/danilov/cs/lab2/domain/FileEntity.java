package by.danilov.cs.lab2.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileEntity {


    private String fileName;
    private String content;

    public FileEntity() {
    }
}
