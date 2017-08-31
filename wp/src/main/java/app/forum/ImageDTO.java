package app.forum;

import java.io.File;

public class ImageDTO {

	private File img;

	public ImageDTO(File img) {
		this.img = img;
	}

	public ImageDTO() {
	}

	public File getImg() {
		return img;
	}

	public void setImg(File img) {
		this.img = img;
	}
	
	
	
}
