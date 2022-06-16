import React from "react"
import {useContext, useRef, useState} from "react"
import {CartContext} from "../App"
import {useRequest} from "ahooks"
import {Button, List, message, Pagination} from "antd"
import "./ProductList.css"

export default function ProductList(props) {
  // 查询结果的过滤条件
  const [filters, setFilters] = useState({
    page: 1,
    pageSize: 8,
    category: "all",
    keyword: ""
  })

  // 从上下文中取操作
  const {requestAddItem} = useContext(CartContext);

  // 记录对象引用
  const listRef = useRef(null)

  // 页面第一次载入或过滤条件变化时从后端取数据
  const {data, error, loading} = useRequest(
    () => loadProducts(filters),
    {refreshDeps: [filters]}
  )

  const loadProducts = async (filters) => {
    // 取查询条件
    const {page, pageSize, category, keyword} = filters
    // 取数据
    return fetch(
      `http://localhost:8080/api/products/pager?page=${page}&pageSize=${pageSize}&category=${category}&keyword=${keyword}`,
      {
        method: "GET"
      }
    ).then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw Error("Failed to load products!")
      }
    })
  }

  if (error) {
    message.error(error.message)
  }

  // 如何渲染列表项
  const renderItem = product => {
    return (
      <List.Item>
        <div className="product-container">
          <div className="product-image-container">
            <img className="product-image" alt={product.image} src={product.image}/>
          </div>
          <div className="product-price">
            {"￥" + product.price.toFixed(1)}
          </div>
          <div className="product-description">
            {product.name}
          </div>
          <div className="product-actions">
            <Button
              className="add-item-button"
              onClick={() => {requestAddItem(product.id, 1)}}
            >
              加入购物车
            </Button>
          </div>
        </div>
      </List.Item>
    )
  }

  return (
    <div className="product-list" ref={listRef}>
      <List
        dataSource={data?.products}
        grid={{column: 4, gutter: 20}}
        loading={loading}
        renderItem={renderItem}
      />
      <Pagination
        defaultCurrent={1}
        defaultPageSize={16}
        current={filters.page}
        pageSize={filters.pageSize}
        total={data?.total}
        hideOnSinglePage={true}
        showQuickJumper={true}
        showTotal={total => <span>共 {total} 条数据</span>}
        style={{textAlign: "center", marginBottom: "30px"}}
        onChange={(page, pageSize) => {
          setFilters({...filters, page, pageSize})
          window.scrollTo(0, 0)
        }}
      />
    </div>
  )
}
