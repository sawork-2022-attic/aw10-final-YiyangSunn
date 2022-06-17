import React, {useContext} from "react"
import "./SideMenu.css"
import {Menu} from "antd"
import {AccountBookOutlined, ShopOutlined} from "@ant-design/icons"
import {GlobalContext} from "../App"
import {Link, useLocation} from "react-router-dom"

export default function SideMenu() {

  const {filters, setFilters, setKeyword} = useContext(GlobalContext)

  const location = useLocation()

  // 点击菜单时的处理函数
  const menuClicked = (item) => {
    if (item.key !== "orders"){
      if (item.key !== "all products") {
        setFilters({...filters, category: item.key, keyword: null, page: 1})
      } else {
        setFilters({...filters, category: null, keyword: null, page: 1})
      }
    }
    // 清空搜索输入框
    setKeyword(null)
  }

  const findSelectedKeys = () => {
    if (location.pathname === "/") {
      return ["all products"]
    } else if (location.pathname === "/sports_and_outdoors") {
      return ["Sports"]
    } else if (location.pathname === "/books") {
      return ["Books"]
    } else if (location.pathname === "/video_games") {
      return ["Video Games"]
    } else if (location.pathname === "/all_electronics") {
      return ["All Electronics"]
    } else if (location.pathname === "/automotive") {
      return ["Automotive"]
    } else if (location.pathname === "/amazon_home") {
      return ["Amazon Home"]
    } else if (location.pathname === "/office_products") {
      return ["Office Products"]
    } else if (location.pathname === "/orders") {
      return ["orders"]
    }
  }

  return (
    <div className="side-menu">
      <Menu
        defaultOpenKeys={["browse products"]}
        defaultSelectedKeys={findSelectedKeys()}
        theme="light"
        mode="inline"
        onClick={menuClicked}
      >
        <Menu.SubMenu
          title={
            <div>
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">浏览商品</span>
            </div>
          }
          key="browse products"
        >
          <Menu.Item className="side-menu-item" key="all products">
            <Link to="/">
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">所有商品</span>
            </Link>
          </Menu.Item>
          <Menu.Item className="size-menu-item" key="Sports">
            <Link to="/sports_and_outdoors">
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">体育户外</span>
            </Link>
          </Menu.Item>
          <Menu.Item className="size-menu-item" key="Books">
            <Link to="/books">
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">图书</span>
            </Link>
          </Menu.Item>
          <Menu.Item className="size-menu-item" key="Video Games">
            <Link to="video_games">
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">视频游戏</span>
            </Link>
          </Menu.Item>
          <Menu.Item className="size-menu-item" key="All Electronics">
            <Link to="all_electronics">
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">电子产品</span>
            </Link>
          </Menu.Item>
          <Menu.Item className="size-menu-item" key="Automotive">
            <Link to="automotive">
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">机动车零部件</span>
            </Link>
          </Menu.Item>
          <Menu.Item className="size-menu-item" key="Amazon Home">
            <Link to="amazon_home">
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">家居</span>
            </Link>
          </Menu.Item>
          <Menu.Item className="size-menu-item" key="Office Products">
            <Link to="office_products">
              <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
              <span className="side-menu-item-label">办公用品</span>
            </Link>
          </Menu.Item>
        </Menu.SubMenu>
        <Menu.Item className="side-menu-item" key="orders">
          <Link to="/orders">
            <AccountBookOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
            <span className="side-menu-item-label">我的订单</span>
          </Link>
        </Menu.Item>
      </Menu>
    </div>
  )
}
