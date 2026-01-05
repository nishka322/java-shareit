package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
@Primary
@Qualifier("DbItemRepo")
public interface DbItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(long userId);

    @Query("""
            SELECT i
            FROM Item i
            WHERE
                (LOWER(i.name) LIKE CONCAT('%', LOWER(:text), '%')
                OR LOWER(i.description) LIKE CONCAT('%', LOWER(:text), '%'))
                AND i.available = true
            """)
    List<Item> searchByText(@Param("text") String text);
}
