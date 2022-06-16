import React from "react"
import "./SideMenu.css"
import {Menu} from "antd"
import {AccountBookOutlined, ShopOutlined} from "@ant-design/icons"
import {Link, useLocation} from "react-router-dom"

export default function SideMenu(props) {
  // 用于判断当前菜单项
  const location = useLocation()

  return (
    <div className="side-menu">
      <Menu theme="light" defaultSelectedKeys={[location.pathname]}>
        <Menu.Item className="side-menu-item" key="/">
          <Link to="/">
            <ShopOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
            <span className="side-menu-item-label">所有商品</span>
          </Link>
        </Menu.Item>
        <Menu.Item className="side-menu-item" key="/orders">
          <Link to="/orders">
            <AccountBookOutlined style={{fontSize: "16px", marginLeft: "25px"}}/>
            <span className="side-menu-item-label">我的订单</span>
          </Link>
        </Menu.Item>
      </Menu>
    </div>
  )
}
