import React, { useState } from 'react'
import SendIcon from './SendIcon';

const Message = () => {
    const [senderMsg, setSenderMsg] = useState("");
    const [error, setError] =  useState(null);
    const [chat, setChat] = useState([
        {
            id:1,
            sender:"user",
            message:"Hi!"
        },
        {
            id:2,
            sender:"server",
            message:"Hello! How can i help you?"
        },
        {
            id:3,
            sender:"user",
            message:"What's the date today?"
        },
        {
            id:4,
            sender:"server",
            message:"It's August 4th, 2025."
        }
    ])

    const addMessage = (e) => {
        e.preventDefault();
        if(senderMsg===""){
             setError("No message found. Message cannot be sent empty.")
             return;
        }
        setError(null);
        setChat(prevChat => {
            return [...prevChat, {id:chat.length+1, sender:'user', message:senderMsg}]
        })
        setSenderMsg("")
    }

    return (
        <div className='px-3 py-3 w-1/2 bg-linear-to-bl from-pink-500 to-purple-500 rounded-[30px] shadow-lg'>
            <ul className='flex flex-col gap-2'>
                {chat.map((msg)=>(
                    <li key={msg.id} className={`flex flex-col space-y-1 ${msg.sender==='user' ? 'self-end' : 'self-start'}`}>
                        <h6 className='text-white'>{msg.sender}</h6>
                        <p className={`px-3 py-2 ${msg.sender==='user' ? 'bg-pink-200 text-pink-500' : 'bg-purple-200 text-purple-500'} font-medium rounded-full transition duration-200`}>{msg.message}</p>
                    </li>
                ))}
            </ul>
            <div className='px-2 py-2 mt-3 rounded-full bg-pink-300 flex flex-row space-x-2 justify-between'>
                <input 
                    type='text' 
                    placeholder='enter your message' 
                    className='w-full px-3 py-2 text-pink-500 font-medium outline-0'
                    value={senderMsg}
                    onChange={(e)=>setSenderMsg(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                        addMessage();
                        }
                    }}
                ></input>
                <button 
                    onClick={addMessage}
                    className='px-4 py-2 text-white bg-purple-500 rounded-full hover:shadow-lg hover:bg-purple-400 cursor-pointer'
                >
                    <SendIcon />
                </button>
            </div>
            {error && 
                <div className='mt-2 px-4 py-2 bg-red-400 text-red-600 font-medium rounded-full transition ease-in duration-200'>
                    <h6>{error}</h6>
                </div>
            }
        </div>
    )
}

export default Message