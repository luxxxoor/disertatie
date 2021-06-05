import time
from locust import HttpUser, task, between
from random import randint

class QuickstartUser(HttpUser):
    wait_time = between(1, 2.5)

    #  @task
    #  def test1(self):
    #      self.client.get("/test1")

    #  @task
    #  def test2(self):
    #      self.client.get(f"/test2?n={randint(100, 15000)}");

    @task
    def test3(self):
        self.client.get(f"/test3?m={randint(1, 3)}&n={randint(1, 10)}");

    #  @task(3)
    #  def view_items(self):
    #      for item_id in range(10):
    #          self.client.get(f"/item?id={item_id}", name="/item")
    #          time.sleep(1)

    #  @task
    #  def test4(self):
    #      pass


    #  def on_start(self):
    #      self.client.post("/login", json={"username":"foo", "password":"bar"})