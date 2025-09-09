package henrique.corrales.bootcamp.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "Book")
@XmlAccessorType(XmlAccessType.FIELD)
@Relation(collectionRelation = "books")
@JsonPropertyOrder({"Identificação", "Autor", "Titulo", "Preço", "DataLancamento", "links"})
public class BookDTO extends RepresentationModel<BookDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Identificação")
    @JsonProperty("Identificação")
    private Long id;

    @XmlElement(name = "Autor")
    @JsonProperty("Autor")
    private String author;

    @XmlElement(name = "DataLancamento")
    @JsonProperty("DataLancamento")
    private Date launchDate;

    @XmlElement(name = "Preço")
    @JsonProperty("Preço")
    private Double price;

    @XmlElement(name = "Titulo")
    @JsonProperty("Titulo")
    private String title;

    @JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "link")
    @JsonProperty("links")
    public List<Link> getXmlLinks() {
        return super.getLinks().toList();
    }

    public BookDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Date getLaunchDate() { return launchDate; }
    public void setLaunchDate(Date launchDate) { this.launchDate = launchDate; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookDTO bookDTO)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, bookDTO.id)
                && Objects.equals(author, bookDTO.author)
                && Objects.equals(launchDate, bookDTO.launchDate)
                && Objects.equals(price, bookDTO.price)
                && Objects.equals(title, bookDTO.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, author, launchDate, price, title);
    }
}