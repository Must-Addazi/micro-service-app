import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-bills',
  standalone: true,
  imports: [CommonModule,HttpClientModule],
  templateUrl: './bills.component.html',
  styleUrl: './bills.component.css'
})
export class BillsComponent implements OnInit {
  bills:any
  constructor(private http:HttpClient){

  }
  ngOnInit(): void {
    this.http.get("http://localhost:8888/BILLING-SERVICE/bills").subscribe({
      next:(data:any)=>{
        this.bills = data._embedded?.bills || [];
        console.log(data)
      },
      error:(err)=>{
        console.log(err)
      }
    })
  }

}
