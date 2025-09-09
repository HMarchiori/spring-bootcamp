package henrique.corrales.bootcamp.config;


import henrique.corrales.bootcamp.data.BookDTO;
import henrique.corrales.bootcamp.data.PersonDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class HateOasConfig {

    @Bean
    public PagedResourcesAssembler<PersonDTO> personDtoPagedResourcesAssembler() {
        // baseUri null = usa request atual quando existir; no service funciona com link explícito
        return new PagedResourcesAssembler<>(
                new HateoasPageableHandlerMethodArgumentResolver(),
                UriComponentsBuilder.fromPath("").build()
        );
    }

    @Bean
    public PagedResourcesAssembler<BookDTO> bookDtoPagedResourcesAssembler() {
        // baseUri null = usa request atual quando existir; no service funciona com link explícito
        return new PagedResourcesAssembler<>(
                new HateoasPageableHandlerMethodArgumentResolver(),
                UriComponentsBuilder.fromPath("").build()
        );
    }
}