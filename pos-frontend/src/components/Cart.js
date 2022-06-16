import React, {useContext} from "react"
import {Button, List, message} from "antd"
import "./Cart.css"
import {CartContext} from "../App"
import {useRequest} from "ahooks"

export default function Cart() {

  // 获取购物车数据以及相关可用操作
  const {
    itemList,
    requestAddItem,
    requestGetItems,
    requestRemoveItem,
    requestEmptyItems,
    requestCheckout
  } = useContext(CartContext)

  // 从后端读取购物车数据
  const {error, loading} = useRequest(requestGetItems)

  if (error) {
    console.log(error.message)
  }

  const renderItem = item => {
    return (
      <List.Item>
        <div className="item-container">
          <div className="item-info">
            <div className="item-image-description">
              <div className="item-image-container">
                <img className="item-image" alt={item.image} src={item.image}/>
              </div>
              <div className="item-description">
                {item.name}
              </div>
            </div>
            <div className="item-price">
              {"￥" + item.price.toFixed(1)}
            </div>
          </div>
          <div className="item-actions">
            <Button
              size="small"
              danger={true}
              onClick={() => requestRemoveItem(item.id)}
            >
              删除
            </Button>
            <div className="item-quantity-container">
              <Button
                size="small"
                shape="circle"
                disabled={item.quantity === 1}
                onClick={() => requestAddItem(item.id, -1)}
              >
                -
              </Button>
              <span className="item-quantity">{item.quantity}</span>
              <Button
                size="small"
                shape="circle"
                onClick={() => requestAddItem(item.id, 1)}
              >
                +
              </Button>
            </div>
          </div>
        </div>
      </List.Item>
    )
  }

  return (
    <div className="cart">
      <div className="item-list">
        <List
          dataSource={itemList}
          grid={{column: 1}}
          loading={loading}
          renderItem={renderItem}
        />
      </div>
      <div className="cart-actions">
        <Button
          danger={true}
          style={{float: "left"}}
          onClick={requestEmptyItems}
          disabled={itemList.length === 0}
        >
          清空
        </Button>
        <Button
          type="primary"
          style={{marginRight: "0"}}
          disabled={itemList.length === 0}
          onClick={() => requestCheckout().then(data => {
            data !== null && message.success("购买成功")
          })}
        >
          结算
        </Button>
      </div>
    </div>
  )
}
