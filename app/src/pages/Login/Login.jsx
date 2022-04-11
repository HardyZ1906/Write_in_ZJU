import { useState } from "react"
import { Input, Button, message } from "antd"
import axios from "axios"

import "./Login.less"

function Login() {
    const [userName, setUserName] = useState("");
    const [mode, setMode] = useState("login");
    const [psw, setPsw] = useState("");
    const [confirmPsw, setConfirmPsw] = useState("");

    const handleLogin = () => {
        if (mode !== "login") {
            setMode("login");
            return;
        }

        let f = new FormData();
        f.append("username", userName);
        f.append("password", psw);
        axios.post("http://124.214.241.122:8000/login", f).then(res => {
            switch(res) {
                case "200": message.success("登录成功"); break;
                case "404": message.error("用户名或密码错误"); break;
                default: message.error("登录失败");
            }
        });
    }

    const handleRegister = () => {
        if (mode !== "register") {
            setMode("register");
            return;
        }
        
        let f = new FormData();
        f.append("username", userName);
        f.append("password", psw);
        axios.post("http://124.214.241.122:8000/register", f).then(res => {
            switch (res) {
                case "200": message.success("注册成功"); break;
                case "404": message.error("用户名重复"); break;
                default: message.error("注册失败");
            }
        });
    }

    const nameChange=(event)=>{
        setUserName(event.target.value)
    }

    const pswChange=(event)=>{
        setPsw(event.target.value)
    }

    const confirmPswChange=(event)=>{
        setConfirmPsw(event.target.value)
    }

    return (
        <div className="container">
            <div className="login-container">
                <div className="left-container">
                    <div className="title displayHandler">写在浙大</div>
                    <Input placeholder="用户名" className="displayHandler" onChange={nameChange}></Input>
                    <Input placeholder="密码" className="displayHandler" type="password" onChange={pswChange}></Input>
                    <Input placeholder="确认密码" onChange={confirmPswChange}
                        className={`${mode === "register" ? "displayHandler" : "noneHandler"}`}></Input>
                    <a className={`${mode === "login" ? "displayHandler" : "noneHandler"}`}>忘记密码</a>
                </div>
                <div className="right-container">
                    <Button className="register" onClick={handleRegister}>注册</Button>
                    <Button className="login" onClick={handleLogin}>登录</Button>
                </div>
            </div>
        </div>
    )
}

export default Login;