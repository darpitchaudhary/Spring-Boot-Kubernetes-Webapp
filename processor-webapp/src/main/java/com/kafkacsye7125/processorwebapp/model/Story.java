package com.kafkacsye7125.processorwebapp.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.UUID;

@ToString
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Story")
public class Story {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
			name = "UUID",
			strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "table_id", updatable = false, nullable = false)
	private UUID table_id;

	@Column(name = "id")
	private Long id;

	@Column(name = "deleted")
	private Boolean deleted;

	@Lob
	@Column(name = "type")
	private String type;

	@Column(name = "by")
	private String by;

	@Column(name = "time")
	private String time;

	@Lob
	@Column(name = "text")
	private String text;

	@Column(name = "dead")
	private Boolean dead;

	@Column(name = "parent")
	private String parent;

	@Column(name = "poll")
	private String poll;

	@Column(name = "kids")
	private Integer[] kids;

	@Lob
	@Column(name = "url")
	private String url;

	@Column(name = "score")
	private Integer score;


	@Column(name = "title")
	private String title;

	@Column(name = "parts")
	private Integer[] parts;

	@Column(name = "descendants")
	private Integer descendants;

}
