package com.chaitanya.domain.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "BOOK")
@JsonInclude(Include.NON_NULL)
public class Book implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "IBSN")
	private String ibsn;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "AUTHOR")
	private String author;
	
	@Transient
	private String rawtags;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
    		name = "BOOK_TAGS",
    		joinColumns = @JoinColumn(name = "BOOK_ID", referencedColumnName = "IBSN"),
    		inverseJoinColumns = @JoinColumn(name = "TAG_ID", referencedColumnName = "ID"))
    private Set<Tag> tags = new HashSet<>();
}
