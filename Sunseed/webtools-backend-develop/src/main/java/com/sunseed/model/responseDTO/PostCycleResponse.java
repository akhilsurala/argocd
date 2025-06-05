package com.sunseed.model.responseDTO;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCycleResponse {
    private Long id;
    private String name;
    private List<String> weeks;
    private List<List<String>> beds;
    private Set<String> crops;

}
