package com.sunseed.repository;

import com.sunseed.entity.StaticPages;
import com.sunseed.enums.PageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface StaticPagesRepository extends JpaRepository<StaticPages, Long> {
    @Query("Select s from StaticPages s where s.pageType=:pageType AND s.hide = false ORDER BY s.createdAt DESC")
    List<StaticPages> getStaticPageByType(PageType pageType);

    @Query("SELECT s FROM StaticPages s WHERE s.hide = false ORDER BY s.createdAt DESC")
    List<StaticPages> getAllUnhideStaticPages();
    
    @Query("SELECT s FROM StaticPages s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY s.createdAt DESC")
    List<StaticPages> getAllPagesWithSearchTextForAdmin(@Param("searchText") String searchText);

}