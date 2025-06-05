package com.sunseed.controller.admin;

import com.sunseed.enums.PageType;
import com.sunseed.model.requestDTO.StaticPagesRequestDto;
import com.sunseed.model.responseDTO.StaticPageResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.StaticPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/v1")
@RestController
@RequiredArgsConstructor
public class StaticPageController {

    private final StaticPageService staticPageService;
    private final ApiResponse apiResponse;

    @PostMapping("/admin/staticPage")
    public ResponseEntity<Object> addStaticPage(@RequestBody StaticPagesRequestDto request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        StaticPageResponseDto staticPageResponseDto = staticPageService.addStaticPage(request, userId);
        return apiResponse.ResponseHandler(true, "staticPage.add", HttpStatus.CREATED, staticPageResponseDto);
    }

    // for admin
    @GetMapping("/admin/staticPage")
    public ResponseEntity<Object> getAllStaticPage(@RequestParam(value = "search", required = false) String search) {
        List<StaticPageResponseDto> allStaticPage = staticPageService.getAllStaticPage(search.toString());
        return apiResponse.ResponseHandler(true, "list.staticPage", HttpStatus.OK, allStaticPage);
    }

    //  *****************  delete static page *********************
    @DeleteMapping("/admin/staticPage/{staticPageId}")
    public ResponseEntity<Object> deleteStaticPage(@PathVariable Long staticPageId) {
        StaticPageResponseDto staticPage = staticPageService.deleteStaticPageById(staticPageId);
        return apiResponse.ResponseHandler(true, "page.delete", HttpStatus.OK, staticPage);
    }

    // ************** update static page ****************
    @PutMapping("/admin/staticPage/{staticPageId}")
    public ResponseEntity<Object> updateStaticPages(@PathVariable Long staticPageId, @RequestBody StaticPagesRequestDto request) {

        StaticPageResponseDto updatedStaticPage = staticPageService.updateStaticPage(request, staticPageId);
        return apiResponse.ResponseHandler(true, "page.update", HttpStatus.OK, updatedStaticPage);
    }

//    // get static page using type
//    @GetMapping("/staticPage/{type}")
//    public ResponseEntity<Object> getStaticPageByType(@PathVariable PageType type) {
//        List<StaticPageResponseDto> staticPageResponseDtoList = staticPageService.getStaticPageByType(type);
//        return apiResponse.ResponseHandler(true, "list.staticPage", HttpStatus.OK, staticPageResponseDtoList);
//    }

    // get  all static pages for user , hide : false
    @GetMapping("/staticPage")
    public ResponseEntity<Object> getAllStaticPageForUsers(@RequestParam(name = "type", required = false) PageType pageType) {
        List<StaticPageResponseDto> allStaticPage = staticPageService.getAllStaticPageForUsers(pageType);
        return apiResponse.ResponseHandler(true, "list.staticPage", HttpStatus.OK, allStaticPage);
    }

    // for admin
    @GetMapping("/admin/staticPage/{staticPageId}")
    public ResponseEntity<Object> getStaticPageById(@PathVariable Long staticPageId) {
        StaticPageResponseDto staticPageResponseDto = staticPageService.getStaticPageById(staticPageId);
        return apiResponse.ResponseHandler(true, "page.fetched", HttpStatus.OK, staticPageResponseDto);

    }

    // for user
    @GetMapping("/staticPage/{staticPageId}")
    public ResponseEntity<Object> getStaticPageByIdForUser(@PathVariable Long staticPageId) {
        StaticPageResponseDto staticPageResponseDto = staticPageService.getStaticPageByIdForUser(staticPageId);
        return apiResponse.ResponseHandler(true, "page.fetched", HttpStatus.OK, staticPageResponseDto);
    }

}
