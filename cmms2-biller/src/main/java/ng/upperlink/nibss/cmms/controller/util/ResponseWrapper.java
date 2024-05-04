package ng.upperlink.nibss.cmms.controller.util;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper {

	private Object content;
	
	private Page<?> page;

	private MetaFields meta = new MetaFields();
	
	public ResponseWrapper(Object content) {
		this.content = content;
	}
	
	public ResponseWrapper(Object content, Page<?> page) {
		this.content = content;
		this.meta.setSize(page.getSize());
		this.meta.setNumber(page.getNumber());
		this.meta.setNumberOfElements(page.getNumberOfElements());
		this.meta.setTotalPages(page.getTotalPages());
		this.meta.setTotalElements(page.getTotalElements());
		this.page = null;
	}

	@Data
	class MetaFields {
		private int size;
		private int number;
		private int numberOfElements;
		private int totalPages;
		private long totalElements;
		private int pageNumber;
		private int pageSize;
	}

}
