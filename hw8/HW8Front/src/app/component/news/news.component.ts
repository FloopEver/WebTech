import { Component, OnInit, Input } from '@angular/core';


@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.css']
})
export class NewsComponent implements OnInit {

	p: number = 1;

	  extend(element){
		  console.log(element.target)
		  let text = element.target.innerText;
		if(text=='Show Detail'){
			element.target.innerText='Hide Detail';
		}else{
			element.target.innerText='Show Detail';
		}
	  }

	  newsdata: any;
	  keyword: string;
	  @Input() set getdad(getdad: any){
	  this.newsdata = getdad[0];
	  this.keyword = getdad[1]
	  }
	
 
  constructor() { }

  ngOnInit():void { }

}
