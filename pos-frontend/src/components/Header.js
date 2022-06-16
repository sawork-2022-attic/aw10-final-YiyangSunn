import React, {useContext} from "react"
import {Badge, Button, Dropdown, Input} from "antd"
import {AudioOutlined, CameraOutlined, ShoppingCartOutlined, WindowsOutlined} from "@ant-design/icons"
import "./Header.css"
import {CartContext} from "../App"
import Cart from "./Cart"

export default function Header() {

  const {itemList} = useContext(CartContext)

  return (
    <div className="header">
      <div className="brand">
        <span className="brand-logo">
          <WindowsOutlined/>
        </span>
        <span className="brand-name">
          MicroPOS
        </span>
      </div>

      {/*顶部搜索框*/}
      <Input.Search
        className="search"
        placeholder="Not Implemented"
        size="large"
        prefix={<AudioOutlined style={{marginRight: "15px"}}/>}
        suffix={<CameraOutlined style={{marginLeft: "15px"}}/>}
        enterButton="搜索"
      />

      {/*购物车下拉菜单按钮*/}
      <div className="cart-part">
        <Dropdown overlay={<Cart/>} placement="bottomLeft" arrow={true}>
          <Badge count={itemList.length}>
            <Button className="cart-button">
              <ShoppingCartOutlined className="cart-button-logo"/>
              <span style={{fontSize: "16px"}}>
                我的购物车
              </span>
            </Button>
          </Badge>
        </Dropdown>
      </div>
    </div>
  )
}
