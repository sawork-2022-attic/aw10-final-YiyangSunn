import React from "react"
import ProductList from "../pages/ProductList"
import "./Content.css"
import SideMenu from "./SideMenu"
import {BrowserRouter, Routes, Route} from "react-router-dom"
import OrderList from "../pages/OrderList"

export default function Content() {
  return (
    <BrowserRouter>
      <div className="content">
        <div className="content-left">
          <SideMenu/>
        </div>
        <div className="content-right">
          <Routes>
            <Route path="/" element={<ProductList/>}/>
            <Route path="/orders" element={<OrderList/>}/>
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  )
}
