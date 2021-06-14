import time
from locust import HttpUser, task, between
from random import randint

countries = [
    "Afghanistan",
    "Algeria",
    "American Samoa",
    "Angola",
    "Anguilla",
    "Argentina",
    "Armenia",
    "Australia",
    "Austria",
    "Azerbaijan",
    "Bahrain",
    "Bangladesh",
    "Belarus",
    "Bolivia",
    "Brazil",
    "Brunei",
    "Bulgaria",
    "Cambodia",
    "Cameroon",
    "Canada",
    "Chad",
    "Chile",
    "China",
    "Colombia",
    "Czech Republic",
    "Dominican Republic",
    "Ecuador",
    "Egypt",
    "Estonia",
    "Ethiopia",
    "Faroe Islands",
    "Finland",
    "France",
    "French Guiana",
    "French Polynesia",
    "Gambia",
    "Germany",
    "Greece",
    "Greenland",
    "Holy See (Vatican City State)",
    "Hong Kong",
    "Hungary",
    "India",
    "Indonesia",
    "Iran",
    "Iraq",
    "Israel",
    "Italy",
    "Japan",
    "Kazakstan",
    "Kenya",
    "Kuwait",
    "Latvia",
    "Liechtenstein",
    "Lithuania",
    "Madagascar",
    "Malawi",
    "Malaysia",
    "Mexico",
    "Moldova",
    "Morocco",
    "Mozambique",
    "Myanmar",
    "Nauru",
    "Nepal",
    "Netherlands",
    "New Zealand",
    "Nigeria",
    "North Korea",
    "Oman",
    "Pakistan",
    "Paraguay",
    "Peru",
    "Philippines",
    "Poland",
    "Puerto Rico",
    "Romania",
    "Runion",
    "Russian Federation",
    "Saint Vincent and the Grenadines",
    "Saudi Arabia",
    "Senegal",
    "Slovakia",
    "South Africa",
    "South Korea",
    "Spain",
    "Sri Lanka",
    "Sudan",
    "Sweden",
    "Switzerland",
    "Taiwan",
    "Tanzania",
    "Thailand",
    "Tonga",
    "Tunisia",
    "Turkey",
    "Turkmenistan",
    "Tuvalu",
    "Ukraine",
    "United Arab Emirates",
    "United Kingdom",
    "United States",
    "Venezuela",
    "Vietnam",
    "Yemen",
    "Yugoslavia",
    "Zambia"
]

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

    #  @task
    #  def test4(self):
    #      pass

    #  @task
    #  def test5(self):
    #      randindex = randint(0, len(countries)-1)
    #      self.client.get(f"/test5?country={countries[randindex]}")


    #  def on_start(self):
    #      self.client.post("/login", json={"username":"foo", "password":"bar"})
