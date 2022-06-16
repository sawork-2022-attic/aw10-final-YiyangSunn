import React, {useState} from "react"
import {ArrowUpOutlined} from "@ant-design/icons"
import {BackTop, Button, message} from "antd"
import Header from "./components/Header"
import Content from "./components/Content"
import "antd/dist/antd.min.css"
import "./App.css"

export const CartContext = React.createContext({});

export default function App() {
  // 购物车清单（状态提升，用于组件间通信）
  const [itemList, setItemList] = useState([])

  // 操作随状态

  // 添加商品
  const addItem = (newItem) => {
    let isNew = true
    for (let idx in itemList) {
      if (itemList[idx].id === newItem.id) {
        isNew = false
        break
      }
    }
    // 如果不是新的，就更新原来的值
    if (!isNew) {
      setItemList(
        itemList.map(
          item => item.id === newItem.id ?
            {...item, quantity: newItem.quantity} :
            item
        )
      )
    } else {
      setItemList([...itemList, newItem]);
    }
  }

  // 删除某件商品
  const removeItem = (productId) => {
    setItemList(itemList.filter(item => item.id !== productId));
  }

  // 清空购物车
  const emptyItems = () => {
    setItemList([]);
  }

  // 封装网络请求
  const requestAddItem = async (productId, quantity) => {
    await fetch(
      `http://localhost:8080/api/carts/${productId}?quantity=${quantity}`,
      {
        method: "POST"
      }
    ).then(response => {
      if (response.ok) {
        return true
      } else {
        throw Error("Failed to add item!")
      }
    }).then(() => {
      // 如果成功再从后端取 item 的信息
      // 其实后端也可以直接返回一个 item 对象，分开取的好处是更灵活
      // 前端可以选择自己维护数据
      fetch(
        `http://localhost:8080/api/carts/${productId}`,
        {
          method: "GET"
        }
      ).then(response => {
        if (response.ok) {
          message.success(quantity > 0 ? "添加成功" : "成功移除一件商品")
          return response.json()
        } else {
          throw Error("Failed to fetch added item!")
        }
      }).then(data => {
        addItem(data)
      })
    }).catch(error => {
      message.error(error.message)
    })
  }

  const requestGetItems = async () => {
    return await fetch(
      "http://localhost:8080/api/carts",
      {
        method: "GET"
      }
    ).then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw Error("Failed to get items!")
      }
    }).then(data => {
      setItemList(data)
    }).catch(error => {
      message.error(error.message)
    })
  }

  const requestRemoveItem = async (productId) => {
    await fetch(
      `http://localhost:8080/api/carts/${productId}`,
      {
        method: "DELETE"
      }
    ).then(response => {
      if (response.ok) {
        message.success("删除成功")
        removeItem(productId)
      } else {
        throw Error("Failed to delete item!")
      }
    }).catch(error => {
      message.error(error.message)
    })
  }

  const requestEmptyItems = async () => {
    await fetch(
      `http://localhost:8080/api/carts`,
      {
        method: "DELETE"
      }
    ).then(response => {
      if (response.ok) {
        message.success("购物车已清空")
        emptyItems()
      } else {
        throw Error("Failed to empty items!")
      }
    }).catch(error => {
      message.error(error.message)
    })
  }

  const requestCheckout = async () => {
    return fetch(
      "http://localhost:8080/api/carts/checkout",
      {
        method: "POST",
        body: null,
        headers: {
          "Content-Type": "application/json"
        }
      }
    ).then(response => {
      if (response.ok) {
        return response.text()
      } else {
        throw new Error("Failed to checkout!")
      }
    }).catch(error => {
      message.error(error.message)
    })
  }

  // 把数据和可用操作封装到上下文中，子组件可以直接从 context 里拿，
  // 不用一层一层传递属性，避免 prop drilling
  const value = {
    itemList,
    requestAddItem,
    requestGetItems,
    requestRemoveItem,
    requestEmptyItems,
    requestCheckout,
  }

  return (
    <div className="app">
      <CartContext.Provider value={value}>
        <Header/>
        <Content/>
        <BackTop style={{width: "auto", height: "auto"}}>
          <Button
            className="back-top-button"
            icon={<ArrowUpOutlined className="back-top-button-icon"/>}
            shape="circle"
          >
          </Button>
        </BackTop>
      </CartContext.Provider>
    </div>
  );
}
