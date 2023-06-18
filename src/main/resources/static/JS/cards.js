const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      cards: [],
    };
  },

  created() {
    axios.get("http://localhost:8080/api/clients/1").then((response) => {
      this.cards = response.data.cards;
      console.log(this.cards);
    });
  },
});

app.mount("#app");
