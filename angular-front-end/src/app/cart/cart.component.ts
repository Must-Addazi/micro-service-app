import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CartService } from '../cart.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent {
  customerId = 1;
  order: any;
  errorMessage = '';
  loading = false;

  constructor(public cart: CartService) {
  }

  checkout(): void {
    if (!this.cart.items.length) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.order = null;

    this.cart.checkout(this.customerId).subscribe({
      next: data => {
        this.order = data;
        this.loading = false;
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Order could not be created.';
        this.loading = false;
      }
    });
  }
}
