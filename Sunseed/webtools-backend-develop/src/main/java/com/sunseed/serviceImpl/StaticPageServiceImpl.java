package com.sunseed.serviceImpl;

import com.sunseed.entity.StaticPages;
import com.sunseed.entity.UserProfile;
import com.sunseed.enums.PageType;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.model.requestDTO.StaticPagesRequestDto;
import com.sunseed.model.responseDTO.StaticPageResponseDto;
import com.sunseed.repository.StaticPagesRepository;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.StaticPageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StaticPageServiceImpl implements StaticPageService {

    private final StaticPagesRepository staticPagesRepository;
    private final ModelMapper modelMapper;
    private final UserProfileRepository userProfileRepository;

    @Override
    public StaticPageResponseDto addStaticPage(StaticPagesRequestDto request, Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        StaticPages staticPages = modelMapper.map(request, StaticPages.class);
        staticPages.setCreatedBy(userProfile);
        StaticPages savedStaticPage = staticPagesRepository.save(staticPages);
        StaticPageResponseDto staticPageResponseDto = modelMapper.map(savedStaticPage, StaticPageResponseDto.class);
        return staticPageResponseDto;
    }

    @Override
    public List<StaticPageResponseDto> getAllStaticPage(String searchTitle) {
    	List<StaticPages> staticPagesList=null;
    	if(searchTitle==null) {
            staticPagesList = staticPagesRepository.findAll();
       }
       else{
           staticPagesList=staticPagesRepository.getAllPagesWithSearchTextForAdmin(searchTitle.toString());
       }
        List<StaticPageResponseDto> staticPageResponseDtoList = staticPagesList.stream().map((staticPage) -> {
            StaticPageResponseDto staticPageResponseDto = modelMapper.map(staticPage, StaticPageResponseDto.class);
            return staticPageResponseDto;
        }).collect(Collectors.toList());
        return staticPageResponseDtoList;
    }

    @Override
    public StaticPageResponseDto deleteStaticPageById(Long staticPageId) {
        StaticPages staticPages = staticPagesRepository.findById(staticPageId).orElseThrow(() -> new ResourceNotFoundException("static.page.notfound"));
        staticPagesRepository.deleteById(staticPageId);
        return modelMapper.map(staticPages, StaticPageResponseDto.class);
    }


    @Override
    public StaticPageResponseDto updateStaticPage(StaticPagesRequestDto request, Long staticPageId) {
        StaticPages staticPages = staticPagesRepository.findById(staticPageId).orElseThrow(() -> new ResourceNotFoundException("static.page.notfound"));
        StaticPages newStaticPage = modelMapper.map(request, StaticPages.class);
        newStaticPage.setId(staticPageId);
        newStaticPage.setCreatedBy(staticPages.getCreatedBy());
        newStaticPage.setCreatedAt(staticPages.getCreatedAt());
        StaticPages updatedStaticPage = staticPagesRepository.save(newStaticPage);
        StaticPageResponseDto staticPageResponseDto = modelMapper.map(updatedStaticPage, StaticPageResponseDto.class);
        return staticPageResponseDto;
    }

//    // static page by type
//    @Override
//    public List<StaticPageResponseDto> getStaticPageByType(PageType pageType) {
//
//        List<StaticPages> staticPagesList = staticPagesRepository.getStaticPageByType(pageType);
//        List<StaticPageResponseDto> staticPageResponseDtoList = staticPagesList.stream().map((staticPage) -> {
//            StaticPageResponseDto staticPageResponseDto = modelMapper.map(staticPage, StaticPageResponseDto.class);
//            return staticPageResponseDto;
//        }).collect(Collectors.toList());
//        return staticPageResponseDtoList;
//    }

    @Override
    public List<StaticPageResponseDto> getAllStaticPageForUsers(PageType pageType) {
        List<StaticPages> staticPagesList = null;
        if (pageType != null) {
            staticPagesList = staticPagesRepository.getStaticPageByType(pageType);
        } else {
            staticPagesList = staticPagesRepository.getAllUnhideStaticPages();
        }
        if (staticPagesList == null) {
            throw new ResourceNotFoundException("static.page.notfound");
        }
        List<StaticPageResponseDto> staticPageResponseDtoList = staticPagesList.stream().map((staticPage) -> {
            StaticPageResponseDto staticPageResponseDto = modelMapper.map(staticPage, StaticPageResponseDto.class);
            return staticPageResponseDto;
        }).collect(Collectors.toList());
        return staticPageResponseDtoList;
    }

    @Override
    public StaticPageResponseDto getStaticPageById(Long staticPageId) {
        StaticPages staticPages = staticPagesRepository.findById(staticPageId).orElseThrow(() -> new ResourceNotFoundException("static.page.notfound"));
        return modelMapper.map(staticPages, StaticPageResponseDto.class);
    }

    @Override
    public StaticPageResponseDto getStaticPageByIdForUser(Long staticPageId) {
        StaticPages staticPages = staticPagesRepository.findById(staticPageId).orElseThrow(() -> new ResourceNotFoundException("static.page.notfound"));
        if (staticPages.getHide() == Boolean.TRUE) {
            throw new UnprocessableException("resource.hide");
        }
        return modelMapper.map(staticPages, StaticPageResponseDto.class);
    }


}
