import axios from 'axios';
import { Component, OnInit, Output ,EventEmitter } from '@angular/core';
import { HttpParams, HttpHeaders, HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import {Router} from "@angular/router";
import { importExpr } from '@angular/compiler/src/output/output_ast';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {
  userForm: FormGroup;
  @Output() SendToFater = new EventEmitter();
  

  onSubmit() {

    let alertMsg:string = "";
    let KeywordDom:any = document.getElementById("input_Keyword"); 
    let kwd = KeywordDom.value;
    if(kwd==""){
      this.SendToFater.emit();
      alertMsg += "<p class='alertMsg' style='background: #FFFBC6;  margin-bottom: 10px'>Please enter a keyword</p>";
    }
    let MinPriceDom:any = document.getElementById("input_MinPrice");
    let MaxPriceDom:any = document.getElementById("input_MaxPrice");    
    let MinPrice = parseFloat(MinPriceDom.value);
    let MaxPrice = parseFloat(MaxPriceDom.value);
    let flag = 0;
    if (MinPriceDom.value)
    {
       if (MinPrice>=0){}
       else{
        if (flag == 0){
          flag = 1
          this.SendToFater.emit();
          alertMsg += "<p class='alertMsg' style='background: #FFFBC6;  margin-bottom: 10px'>Please use appropriate values for minPrice/MaxPrice</p>";
         }
       }
    }
    if (MaxPriceDom.value)
    {
       if (MaxPrice>=0){}
       else{
        if (flag == 0){
          flag = 1
          this.SendToFater.emit();
          alertMsg += "<p class='alertMsg' style='background: #FFFBC6;  margin-bottom: 10px'>Please use appropriate values for minPrice/MaxPrice</p>";
         }
        }       
    }


    if(MinPrice>MaxPrice){
      this.SendToFater.emit();
      alertMsg += "<p class='alertMsg' style='background: #FFFBC6;  margin-bottom: 10px'>Please use appropriate values for minPrice/MaxPrice</p>";
    }
    
    if(alertMsg==""){
      let data: any = Object.assign(this.userForm.value);
      this.http.post('https://homework8-yijing.wl.r.appspot.com', data).subscribe((rep:any) => {
        if(rep["result"]=="No Result Found" || rep["result"]=="Failure"){
          this.SendToFater.emit();
          alertMsg += "<p class='alertMsg' style='background: #FFFBC6;  margin-bottom: 10px'>No Result Found</p>";
          document.getElementById("alertMsgDiv").innerHTML=alertMsg;
        }else{
          this.SendToFater.emit([rep, this.userForm.value.input_Keyword]);

        }
      }, error => {
      });
    }

   
    document.getElementById("alertMsgDiv").innerHTML=alertMsg;
  }


  reset(){
    location.reload();
  }

  ngOnInit(): void {
  
   this.userForm = this.formBuilder.group({
     input_Keyword: [""],
     input_MinPrice: [""],
     input_MaxPrice: [""],
     condiCheck1: [""],
     condiCheck2: [""],
     condiCheck3: [""],
     condiCheck4: [""],
     condiCheck5: [""],
     sellerCheck: [""],
     shipCheck1: [""],
     shipCheck2: [""],
     sort: ["Best Match"]
   });
  
  }


constructor(private formBuilder: FormBuilder, private http: HttpClient, private router: Router)
  {
   this.http.get('/').subscribe((data:any) => {
    console.log("Send GET request to sever");
    }, error => {      
        console.log("error");
    });
  }


}
