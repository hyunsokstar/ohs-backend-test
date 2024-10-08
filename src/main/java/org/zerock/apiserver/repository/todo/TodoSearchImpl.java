package org.zerock.apiserver.repository.todo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.apiserver.domain.QTodo;
import org.zerock.apiserver.domain.Todo;
import org.zerock.apiserver.dto.PageResponseDTO;
import org.zerock.apiserver.dto.SearchRequestDTO;
import org.zerock.apiserver.dto.TodoDTO;
import org.zerock.apiserver.dto.mapper.TodoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);  // 엔티티 클래스를 명시적으로 전달
    }

    public PageResponseDTO<TodoDTO> search(SearchRequestDTO searchRequestDTO) {
        log.info("search with SearchRequestDTO...........");

        BooleanBuilder builder = getSearchConditions(searchRequestDTO);
        JPQLQuery<Todo> query = from(QTodo.todo).where(builder);

        Pageable pageable = getPageable(searchRequestDTO);

        // 페이징 적용
        this.getQuerydsl().applyPagination(pageable, query);

        // 쿼리 실행 및 결과 반환
        List<TodoDTO> dtoList = executeQueryAndConvertToDto(query);
        long total = query.fetchCount();

        return buildPageResponseDTO(searchRequestDTO, dtoList, total);
    }

    private BooleanBuilder getSearchConditions(SearchRequestDTO searchRequestDTO) {
        BooleanBuilder builder = new BooleanBuilder();
        QTodo todo = QTodo.todo;

        String keyword = searchRequestDTO.getSearchKeyword();
        if (keyword != null && !keyword.isEmpty()) {
            switch (searchRequestDTO.getSearchType()) {
                case "title":
                    builder.and(todo.title.contains(keyword));
                    break;
                case "content":
                    builder.and(todo.content.contains(keyword));
                    break;
                default:
                    builder.and(todo.title.contains(keyword)
                            .or(todo.content.contains(keyword)));
            }
        }
        return builder;
    }

    private Pageable getPageable(SearchRequestDTO searchRequestDTO) {
        return PageRequest.of(
                searchRequestDTO.getPage() - 1,
                searchRequestDTO.getSize(),
                Sort.by("tno").descending()
        );
    }

    private List<TodoDTO> executeQueryAndConvertToDto(JPQLQuery<Todo> query) {
        return query.fetch().stream()
                .map(TodoMapper::entityToDto)
                .collect(Collectors.toList());
    }

    private PageResponseDTO<TodoDTO> buildPageResponseDTO(SearchRequestDTO searchRequestDTO, List<TodoDTO> dtoList, long total) {
        return PageResponseDTO.<TodoDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(searchRequestDTO)
                .total(total)
                .build();
    }

//    public TodoDTO entityToDto(Todo todo) {
//        return TodoDTO.builder()
//                .tno(todo.getTno())
//                .title(todo.getTitle())
//                .content(todo.getContent())
//                .complete(todo.isComplete())
//                .dueDate(todo.getDueDate())
//                .build();
//    }
}
