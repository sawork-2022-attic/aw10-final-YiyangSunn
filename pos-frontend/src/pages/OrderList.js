import React, {useState} from "react"
import "./OrderList.css"
import {useRequest} from "ahooks"
import {Button, Descriptions, message, Modal, Table, Timeline} from "antd"
import {ClockCircleOutlined, ReloadOutlined} from "@ant-design/icons"

// helper functions
const truncateID = id => id.substring(id.length - 16)

const formatUTCMillis = timeMillis => (
  new Date(timeMillis)
    .toISOString()
    .replace(/T/, " ")
    .replace(/\..+/, "")
)

export default function OrderList() {
  // 用于物流详情显示
  const [deli, setDeli] = useState({visible: false, data: null})

  // 用于订单详情显示
  const [ord, setOrd] = useState({visible: false, data: null})

  // 从后端获取订单列表
  const loadOrders = async () => {
    return await fetch(
      "http://localhost:8080/api/orders",
      {
        method: "GET"
      }
    ).then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error("Failed to load orders!")
      }
    })
  }
  
  // 获取订单详情
  const getOrderDetail = async orderId => {
    return fetch(
      `http://localhost:8080/api/orders/${orderId}`,
      {
        method: "GET"
      }
    ).then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error("Failed to get order detail!")
      }
    }).catch(error => {
      message.error(error.message)
    })
  }

  // 获取物流详情
  const getDeliveryDetail = async orderId => {
    return fetch(
      `http://localhost:8080/api/delivery?orderId=${orderId}`,
      {
        method: "GET"
      }
    ).then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error("Failed to get delivery status!")
      }
    }).catch(error => {
      message.error(error.message)
    })
  }

  const {data, error, loading, runAsync} = useRequest(loadOrders)

  if (error) {
    message.error(error.message)
  }

  // 定义表格列以及每一列的渲染方式
  const columns = [
    {
      title: "订单编号",
      dataIndex: "orderId",
      key: "orderId",
      render: orderId => <span>{truncateID(orderId)}</span>
    },
    {
      title: "订单总额",
      dataIndex: "total",
      key: "total",
      render: total => <span>{"￥" + total.toFixed(1)}</span>
    },
    {
      title: "创建日期",
      dataIndex: "createdTime",
      key: "createdTime",
      render: timeMillis => (<span>{formatUTCMillis(timeMillis)}</span>)
    },
    {
      title: "订单状态",
      dataIndex: "orderStatus",
      key: "orderStatus",
      render: orderStatus => {
        const color = orderStatus === "已支付" ? "#0abf5b" : "volcano"
        return (
          <span style={{color}}>{orderStatus}</span>
        )
      }
    },
    {
      title: "操作",
      key: "operation",
      render: (_, outline) => (
        <div>
          <Button
            type="link"
            style={{padding: 0, height: "100%", marginRight: "20px"}}
            onClick={() => {
              getOrderDetail(outline.orderId).then(data => {
                setOrd({...ord, visible: true, data})
              })
            }}
          >
            订单详情
          </Button>
          <Button
            type="link"
            style={{padding: 0, height: "100%"}}
            onClick={() => {
              getDeliveryDetail(outline.orderId).then(data => {
                setDeli({...deli, visible: true, data})
              })
            }}
          >
            物流状态
          </Button>
        </div>
      )
    }
  ]

  return (
    <div className="order-list">
      <Table
        title={() => (
          <div>
            <span className="order-list-title">我的订单</span>
            <Button
              shape="circle"
              style={{marginLeft: "10px"}}
              onClick={runAsync}
              icon={<ReloadOutlined style={{fontSize: "18px"}}/>}
            />
          </div>
        )}
        dataSource={data}
        columns={columns}
        loading={loading}
        rowKey={order => order.orderId}
      />
      <OrderDetail
        ord={ord}
        setOrd={setOrd}
      />
      <DeliveryDetail
        deli={deli}
        setDeli={setDeli}
      />
    </div>
  )
}

function OrderDetail(props) {

  const {ord, setOrd} = props

  const {visible, data} = ord

  return (
    <div className="order-detail">
      {
        visible &&
        <Modal
          width={600}
          title={<span>订单详情</span>}
          centered={true}
          footer={null}
          visible={true}
          onCancel={() => setOrd({...ord, visible: false})}
        >
          <Descriptions layout="vertical" bordered={true}>
            <Descriptions.Item label="订单号">{truncateID(data.orderId)}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{formatUTCMillis(data.createdTime)}</Descriptions.Item>
            <Descriptions.Item label="订单总额">{"￥" + data.total.toFixed(2)}</Descriptions.Item>
            <Descriptions.Item label="订单状态">{data.orderStatus}</Descriptions.Item>
            <Descriptions.Item label="付款时间" span={2}>{formatUTCMillis(data.payedTime)}</Descriptions.Item>
            <Descriptions.Item label="购买的商品">
              {
                data.items.map(item => (
                  <div className="order-detail-item" key={item.id}>
                    <div className="item-image-name">
                      <img alt={item.image} src={item.image} className="item-image"/>
                      <div className="item-name">{item.name}</div>
                    </div>
                    <div className="item-price-quantity">
                      <span className="item-price">单价: {"￥" + item.price.toFixed(2)}</span>
                      <span className="item-quantity">购买数量: {item.quantity}</span>
                    </div>
                  </div>
                ))
              }
            </Descriptions.Item>
          </Descriptions>
        </Modal>
      }
    </div>
  )
}

function DeliveryDetail(props) {

  const {deli, setDeli} = props

  const {visible, data} = deli

  return (
    <div className="delivery-detail">
      {
        visible &&
        <Modal
          width={600}
          title={
            <div className="modal-header">
              <div className="delivery-detail-title">物流状态</div>
              <div className="delivery-header-info">订单号：{truncateID(data.orderId)}</div>
              <div className="delivery-header-info">物流号：{truncateID(data.deliveryId)}</div>
              <div className="delivery-header-info">承运人：{data.carrier}</div>
            </div>
          }
          centered={true}
          footer={null}
          visible={true}
          onCancel={() => setDeli({...deli, visible: false})}
        >
          <div className="delivery-timeline">
            <Timeline mode="left" style={{width: "500px"}}>
              {
                data.phases.map(p => (
                  <Timeline.Item
                    key={p.phaseId}
                    label={
                      <div>
                        <ClockCircleOutlined style={{fontSize: "16px", color: "#1890ff", marginRight: "6px"}}/>
                        <span>{formatUTCMillis(p.timeStamp)}</span>
                      </div>
                    }
                    color="green"
                  >
                    {p.message}
                  </Timeline.Item>
                ))
              }
            </Timeline>
          </div>
        </Modal>
      }
    </div>
  )
}
