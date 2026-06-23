import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface Product {
  id: string;
  name: string;
  description?: string;
  category?: string;
  imageUrl?: string;
  price: number;
  quantity: number;
}

export interface CartItem {
  id?: number;
  productId?: string;
  product: Product;
  quantity: number;
  unitPrice?: number;
  lineTotal?: number;
}

export interface CartResponse {
  items: CartItem[];
  count: number;
  total: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly apiUrl = 'http://localhost:8888/billing-service/cart';
  private readonly itemsSubject = new BehaviorSubject<CartItem[]>([]);
  private countValue = 0;
  private totalValue = 0;
  readonly items$ = this.itemsSubject.asObservable();

  constructor(private http: HttpClient) {
    this.load();
  }

  get items(): CartItem[] {
    return this.itemsSubject.value;
  }

  get count(): number {
    return this.countValue;
  }

  get total(): number {
    return this.totalValue;
  }

  add(product: Product): void {
    this.http.post<CartResponse>(`${this.apiUrl}/items`, {
      productId: product.id,
      quantity: 1
    }, { withCredentials: true }).subscribe(response => this.setCart(response));
  }

  updateQuantity(productId: string, quantity: number): void {
    const nextQuantity = Math.max(1, quantity);
    this.http.patch<CartResponse>(`${this.apiUrl}/items/${productId}`, {
      productId,
      quantity: nextQuantity
    }, { withCredentials: true }).subscribe(response => this.setCart(response));
  }

  remove(productId: string): void {
    this.http.delete<CartResponse>(`${this.apiUrl}/items/${productId}`, {
      withCredentials: true
    }).subscribe(response => this.setCart(response));
  }

  clear(): void {
    this.http.delete<CartResponse>(this.apiUrl, {
      withCredentials: true
    }).subscribe(response => this.setCart(response));
  }

  checkout(customerId: number) {
    return this.http.post<any>(`${this.apiUrl}/checkout`, { customerId }, {
      withCredentials: true
    }).pipe(tap(() => this.setCart({ items: [], count: 0, total: 0 })));
  }

  load(): void {
    this.http.get<CartResponse>(this.apiUrl, {
      withCredentials: true
    }).subscribe(response => this.setCart(response));
  }

  private setCart(cart: CartResponse): void {
    this.countValue = cart.count;
    this.totalValue = cart.total;
    this.itemsSubject.next(cart.items);
  }
}
