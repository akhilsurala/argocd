package com.sunseed.service;

import com.sunseed.enums.PageType;
import com.sunseed.model.requestDTO.StaticPagesRequestDto;
import com.sunseed.model.responseDTO.StaticPageResponseDto;

import java.util.List;


public interface StaticPageService {
    StaticPageResponseDto addStaticPage(StaticPagesRequestDto request, Long userId);

    List<StaticPageResponseDto> getAllStaticPage(String searchTitle);

    StaticPageResponseDto deleteStaticPageById(Long staticPageId);

    StaticPageResponseDto updateStaticPage(StaticPagesRequestDto request, Long staticPageId);

//    List<StaticPageResponseDto> getStaticPageByType(PageType pageType);

    List<StaticPageResponseDto> getAllStaticPageForUsers(PageType pageType);
    StaticPageResponseDto getStaticPageById(Long staticPageId);
    StaticPageResponseDto getStaticPageByIdForUser(Long staticPageId);

}