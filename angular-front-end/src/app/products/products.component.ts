import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CartService, Product } from '../cart.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule,HttpClientModule,FormsModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  search = '';
  selectedCategory = 'All';

  constructor(private http:HttpClient, public cart: CartService){

  }
  ngOnInit(): void {
this.http.get("http://localhost:8888/inventory-service/products").subscribe({
  next:(data:any)=>{
    this.products = data._embedded?.products || [];
    console.log(data)
  },error:(err)=>{
    console.log(err)
  }
})
  }

  get categories(): string[] {
    return ['All', ...new Set(this.products.map(product => product.category || 'Other'))];
  }

  get filteredProducts(): Product[] {
    const query = this.search.trim().toLowerCase();
    return this.products.filter(product => {
      const matchCategory = this.selectedCategory === 'All' || product.category === this.selectedCategory;
      const matchSearch = !query ||
        product.name.toLowerCase().includes(query) ||
        (product.description || '').toLowerCase().includes(query);
      return matchCategory && matchSearch;
    });
  }

  addToCart(product: Product): void {
    if (product.quantity > 0) {
      this.cart.add(product);
    }
  }
}
