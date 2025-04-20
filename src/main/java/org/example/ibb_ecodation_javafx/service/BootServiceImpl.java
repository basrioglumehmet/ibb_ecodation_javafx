package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.result.BootResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BootServiceImpl implements BootService{
    private final MailService mailService;
    @Override
    public BootResult runMailTest() {
       var response = mailService.sendTestMail();
       if(response.isSuccess()){

           return new BootResult(
                   true,
                   "Mail Başarılı"
           );
       }

        return new BootResult(
                false,
                response.message()
        );
    }
}
