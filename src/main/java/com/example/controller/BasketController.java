package com.example.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entity.Basket;
import com.example.entity.BasketView;
import com.example.entity.ClassImage;
import com.example.entity.Member;
import com.example.repository.BasketRepository;
import com.example.repository.MemberRepository;
import com.example.service.basket.BasketService;
import com.example.service.classproduct.ClassManageService;
import com.example.service.classproduct.ClassUnitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(value = "/member")
@RequiredArgsConstructor

public class BasketController {

    final String format = "BasketController => {}";
    final BasketRepository bRepository;
    final MemberRepository mRepository;
    @Autowired BasketService bService;
    @Autowired ClassUnitService unitService;
    @Autowired ClassManageService classManageService;
    

    // 장바구니 페이지 접속 및 리스트 출력
    // 127.0.0.1:8080/specialday/member/basket.do

    @GetMapping(value = "/basket.do")
    public String  BasketGET(Model model,
    @AuthenticationPrincipal User user,
    @RequestParam(name = "no", defaultValue = "0")long no ) {

        log.info(format,user.getUsername());
        
        //회원정보
        Member member = mRepository.findById(user.getUsername()).orElse(null);
        log.info(format, member.getId());  //오류발생시점 stackoverflow
        
        List<BasketView> list = bRepository.findByMemberidOrderByNoDesc(member.getId());
        long mainImg = classManageService.selectClassMainImageNo(no);

        for(BasketView  obj: list ){    //반복문을 이용한다
            
            log.info(format, obj.getNo());
        }

            
        model.addAttribute("list", list);
        model.addAttribute("user", user);
        model.addAttribute("mainImg", mainImg);

        log.info(format, list.toString());

        return "/member/basket";
        
    }

     

    // @GetMapping(value = "/basket.do")
    // public ResponseEntity<byte[]> image(@RequestParam(name = "no", defaultValue = "0") long no) throws IOException {
    //     ClassImage obj = classManageService.selectClassMainImageNo(no);
    //     HttpHandler handlers = new HttpHeaders();

    //     if(obj != null) {
    //         if(obj.getFilesize() > 0) {
    //             headers.setContentType( MediaType.parseMediaType( obj.getFiletype() ) );
    //             return new ResponseEntity<>(obj.getFiledata(),headers, HttpStatus.OK);
    //         }
    //     }
    //     //이미지가 없을 경우
    //     InputStream is = resourceLoader.getResource(defaultImage).getInputStream(); // exception 발생됨.
    //     headers.setContentType(MediaType.IMAGE_PNG);
    //     return new ResponseEntity<>( is.readAllBytes(), headers, HttpStatus.OK );
    // }


    @PostMapping(value = "/basket.do")
    public String BasketPOST(Basket basket, HttpServletRequest request) {
        // 로그인 체크
        HttpSession session = request.getSession();
        Member id = (Member)session.getAttribute("member");
        if(id == null) {
            return "0";
        }

        // 카드 등록

        int result = bService.insertBasketOne(basket);

        return result + "";

    }

    // 3. 상품 정보 수정
    @PostMapping(value = "/basket/update")
    public String updateBasketPOST(Basket obj) {

        bService.updateBasketOne(obj);

        return "redirect:/basket/" + obj.getMember(); 

    }

    // 3. 상품 정보 삭제
    @PostMapping(value = "/basket")
    public String deleteBasketPOST(int obj) {

        bService.deleteBasketOne(obj);

        return "redirect:/basket/" + obj; 

    }

    
}
