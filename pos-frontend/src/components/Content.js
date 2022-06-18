import React from "react"
import ProductList from "../pages/ProductList"
import "./Content.css"
import SideMenu from "./SideMenu"
import {Routes, Route} from "react-router-dom"
import OrderList from "../pages/OrderList"

export default function Content() {
  return (
    <div className="content">
      <div className="content-left">
        <SideMenu/>
      </div>
      <div className="content-right">
        <Routes>
          <Route path="/sports_and_outdoors" element={<ProductList/>}/>
          <Route path="/books" element={<ProductList/>}/>
          <Route path="/video_games" element={<ProductList/>}/>
          <Route path="/all_electronics" element={<ProductList/>}/>
          <Route path="/automotive" element={<ProductList/>}/>
          <Route path="/amazon_home" element={<ProductList/>}/>
          <Route path="/office_products" element={<ProductList/>}/>
          <Route path="/orders" element={<OrderList/>}/>
          <Route path="/" element={<ProductList/>}/>
        </Routes>
      </div>
    </div>
  )
}
