package Book.and.Loaning.Management.System.Mapping;
import Book.and.Loaning.Management.System.DTO.BookDTO;
import Book.and.Loaning.Management.System.Entity.BookEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDTO toDTO(BookEntity entity);
    BookEntity toEntity(BookDTO dto);
}
