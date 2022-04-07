# Write_in_ZJU

## 项目运行
建议使用yarn来管理package

```
npm i yarn -g
cd app
yarn
yarn start
```

## 文件架构
pages文件夹中存放页面，请按如下方式：
```
-pages
    |
    -Login
        |
        -Login.jsx      //源代码
        -Login.less     //样式表
    |
    -XXX页面
        |
        -XXX页面.jsx    //源代码
        -XXX页面.less   //样式表
```

components文件夹中存放自己封装的组件（如果有需要的话）。文件架构同上。
```
-components
    |
    -XXX
        |
        -XXX.jsx        //组件源代码
        -XXX.less       //组件的样式
```