package io.spring.model.file;

import java.util.Date;

import io.spring.model.goods.entity.Itasrt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileVo {
	private String fileName;
	private String originalFileName;
	private String filePath;
	private String fileType;
	private long fileSize;
}
