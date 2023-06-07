const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      clients: [],
      accounts: [],
    };
  },

  created() {
    axios.get("http://localhost:8080/api/clients/1").then((response) => {
      this.clients = response.data;
      console.log(this.clients);
      this.accounts = this.clients.accounts;
    });
  },
});

app.mount("#app");
