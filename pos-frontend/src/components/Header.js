import React, {useContext} from "react"
import {Badge, Button, Dropdown, Input} from "antd"
import {AudioOutlined, CameraOutlined, ShoppingCartOutlined, WindowsOutlined} from "@ant-design/icons"
import "./Header.css"
import {GlobalContext} from "../App"
import Cart from "./Cart"
import {useLocation} from "react-router-dom"

export default function Header() {

  const location = useLocation()

  const {itemList, filters, setFilters, keyword, setKeyword} = useContext(GlobalContext)

  return (
    <div className="header">
      <div className="brand">
        <span className="brand-logo">
          <WindowsOutlined/>
        </span>
        <span className="brand-name">
          MicroPoS
        </span>
      </div>

      {/*顶部搜索框*/}
      <Input.Search
        value={keyword}
        onChange={e => setKeyword(e.target.value)}
        className="search"
        placeholder="请输入商品名称"
        size="large"
        prefix={<AudioOutlined style={{marginRight: "15px"}}/>}
        suffix={<CameraOutlined style={{marginLeft: "15px"}}/>}
        enterButton="搜索"
        disabled={location.pathname === "/orders"}
        onSearch={(keyword) => {
          if (keyword.trim() !== ""){
            setFilters({...filters, keyword, page: 1})
          } else {
            setFilters({...filters, keyword: null, page: 1})
          }
        }}
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
