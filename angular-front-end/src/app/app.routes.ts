import { Routes } from '@angular/router';
import { CustomersComponent } from './customers/customers.component';
import { ProductsComponent } from './products/products.component';
import { BillsComponent } from './bills/bills.component';
import { CartComponent } from './cart/cart.component';

export const routes: Routes = [
{ path : "", redirectTo:"products", pathMatch:"full"},
{ path : "customers", component:CustomersComponent},
{ path : "products", component:ProductsComponent },
{ path : "bills", component:BillsComponent },
{ path : "cart", component:CartComponent }
];
