import { useState } from 'react'
import { Input, Button } from 'antd'
import './Login.less'

function Login() {
    const [userName, setUserName] = useState('');
    const [mode, setMode] = useState('login');
    const [psw, setPsw] = useState('');
    const [confirmPsw, setConfirmPsw] = useState('');

    const handleLogin = () => {
        if (mode !== 'login') {
            setMode('login');
            return;
        }


    }

    const handleRegister = () => {
        if (mode !== 'register') {
            setMode('register');
            return;
        }


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
        <div className='container'>
            <div className='login-container'>
                <div className='left-container'>
                    <div className='title displayHandler'>写在浙大</div>
                    <Input placeholder='用户名' className='displayHandler' onChange={nameChange}></Input>
                    <Input placeholder='密码' className='displayHandler' type='password' onChange={pswChange}></Input>
                    <Input placeholder='确认密码' onChange={confirmPswChange}
                        className={`${mode === 'register' ? 'displayHandler' : 'noneHandler'}`}></Input>
                    <a className={`${mode === 'login' ? 'displayHandler' : 'noneHandler'}`}>忘记密码</a>
                </div>
                <div className='right-container'>
                    <Button className='register' onClick={handleRegister}>注册</Button>
                    <Button className='login' onClick={handleLogin}>登陆</Button>
                </div>
            </div>
        </div>
    )
}

export default Login;