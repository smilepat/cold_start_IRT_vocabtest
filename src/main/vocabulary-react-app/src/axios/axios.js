import axios from "axios";

const instance = axios.create({

  baseURL: "http://175.125.92.87:8080"
});

export default instance;