import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule,HttpClientModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit {
  products:any
  constructor(private http:HttpClient){

  }
  ngOnInit(): void {
this.http.get("http://localhost:8888/INVENTORY-SERVICE/products").subscribe({
  next:(data:any)=>{
    this.products = data._embedded?.products || [];
    console.log(data)
  },error:(err)=>{
    console.log(err)
  }
})
  }

}
