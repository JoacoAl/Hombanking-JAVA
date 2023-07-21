const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      client: [],
      logged: false,
    };
  },

  created() {
    axios.get("http://localhost:8080/api/clients/current").then((response) => {
      console.log(response);
      this.client = response.data;
      this.logged = true;
      console.log(this.client);
    });
  },

  methods: {
    logOut() {
      axios.post("/api/logout").then((response) => {
        console.log("signed out!!!");
        this.logged = false;
      });
    },
  },
});

app.mount("#app");

