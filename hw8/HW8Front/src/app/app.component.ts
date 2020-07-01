import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  
  title = 'HW8';
  getson:any;
  getdata(event){
  	this.getson=event;
  	console.log("this.getson");
  	console.log(this.getson);

  }
}
