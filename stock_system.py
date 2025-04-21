import csv
import os
from datetime import datetime

class CustomHeap:
    def __init__(self, is_max_heap=False):
        self.heap = []
        self.is_max_heap = is_max_heap

    def push(self, item):
        self.heap.append(item)
        self._heapify_up()

    def pop(self):
        if not self.heap:
            return None
        if len(self.heap) == 1:
            return self.heap.pop()
        root = self.heap[0]
        self.heap[0] = self.heap.pop()
        self._heapify_down()
        return root

    def peek(self):
        return self.heap[0] if self.heap else None

    def _heapify_up(self):
        idx = len(self.heap) - 1
        while idx > 0:
            parent = (idx - 1) // 2
            if (self.is_max_heap and self.heap[idx][0] > self.heap[parent][0]) or \
               (not self.is_max_heap and self.heap[idx][0] < self.heap[parent][0]):
                self.heap[idx], self.heap[parent] = self.heap[parent], self.heap[idx] 
                idx = parent
            else:
                break

    def _heapify_down(self):
        idx = 0
        while 2 * idx + 1 < len(self.heap):
            child = 2 * idx + 1
            if child + 1 < len(self.heap) and ((self.is_max_heap and self.heap[child + 1][0] > self.heap[child][0]) or
                                               (not self.is_max_heap and self.heap[child + 1][0] < self.heap[child][0])):
                child += 1
            if (self.is_max_heap and self.heap[idx][0] < self.heap[child][0]) or \
               (not self.is_max_heap and self.heap[idx][0] > self.heap[child][0]):
                self.heap[idx], self.heap[child] = self.heap[child], self.heap[idx]
                idx = child
            else:
                break

class TransactionTracker:
    def __init__(self):
        self.min_heap = CustomHeap()  # Min-heap for lowest price transactions
        self.max_heap = CustomHeap(is_max_heap=True)  # Max-heap for highest price transactions
        self.all_transactions = []  # Full transaction history

    def add_transaction(self, stock_name, price, transaction_type, timestamp=None):
        if timestamp is None:
            timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        transaction = (price, stock_name, transaction_type, timestamp)
        self.min_heap.push(transaction)
        self.max_heap.push((-price, stock_name, transaction_type, timestamp))  # Store negative price for max heap
        self.all_transactions.append(transaction)

    def get_lowest_transaction(self):
        transaction = self.min_heap.peek()
        if transaction:
            price, stock_name, transaction_type, timestamp = transaction
            return price, stock_name, transaction_type, timestamp
        return None

    def get_highest_transaction(self):
        transaction = self.max_heap.peek()
        if transaction:
            price, stock_name, transaction_type, timestamp = transaction
            return -price, stock_name, transaction_type, timestamp
        return None

    def get_full_transaction_history(self):
        return sorted(self.all_transactions, key=lambda x: x[3])

    def load_transactions_from_csv(self, file_path):
        if os.path.exists(file_path):
            with open(file_path, newline='', encoding='utf-8') as csvfile:
                reader = csv.reader(csvfile)
                next(reader, None)  # Skip header if present
                for row in reader:
                    if len(row) == 4:
                        stock_name, price, transaction_type, timestamp = row
                        self.add_transaction(stock_name, float(price), transaction_type, timestamp)

    def save_transactions_to_csv(self, file_path):
        with open(file_path, 'w', newline='', encoding='utf-8') as csvfile:
            writer = csv.writer(csvfile)
            writer.writerow(["Stock Name", "Price", "Type", "Timestamp"])
            for price, stock_name, transaction_type, timestamp in self.all_transactions:
                writer.writerow([stock_name, price, transaction_type, timestamp])

class Stock:
    def __init__(self, symbol, price, quantity):
        self.symbol = symbol
        self.price = price
        self.quantity = quantity

    def __str__(self):
        return f"{self.symbol}: Price=${self.price}, Quantity={self.quantity}"

class AVLTree:
    class Node:
        def __init__(self, stock):
            self.stock = stock
            self.left = None
            self.right = None
            self.height = 1

    def __init__(self):
        self.root = None

    def get_height(self, node):
        return node.height if node else 0

    def get_balance(self, node):
        return self.get_height(node.left) - self.get_height(node.right) if node else 0

    def rotate_right(self, y):
        x = y.left
        T2 = x.right
        x.right = y
        y.left = T2
        y.height = 1 + max(self.get_height(y.left), self.get_height(y.right))
        x.height = 1 + max(self.get_height(x.left), self.get_height(x.right))
        return x

    def rotate_left(self, x):
        y = x.right
        T2 = y.left
        y.left = x
        x.right = T2
        x.height = 1 + max(self.get_height(x.left), self.get_height(x.right))
        y.height = 1 + max(self.get_height(y.left), self.get_height(y.right))
        return y

    def insert(self, stock):
        self.root = self._insert(self.root, stock)

    def _insert(self, node, stock):
        if not node:
            return self.Node(stock)
        if stock.symbol < node.stock.symbol:
            node.left = self._insert(node.left, stock)
        else:
            node.right = self._insert(node.right, stock)

        node.height = 1 + max(self.get_height(node.left), self.get_height(node.right))
        balance = self.get_balance(node)

        if balance > 1 and stock.symbol < node.left.stock.symbol:
            return self.rotate_right(node)
        if balance < -1 and stock.symbol > node.right.stock.symbol:
            return self.rotate_left(node)
        if balance > 1 and stock.symbol > node.left.stock.symbol:
            node.left = self.rotate_left(node.left)
            return self.rotate_right(node)
        if balance < -1 and stock.symbol < node.right.stock.symbol:
            node.right = self.rotate_right(node.right)
            return self.rotate_left(node)
        return node

    def search(self, symbol):
        return self._search(self.root, symbol)

    def _search(self, node, symbol):
        if not node:
            return None
        if node.stock.symbol == symbol:
            return node.stock
        elif symbol < node.stock.symbol:
            return self._search(node.left, symbol)
        else:
            return self._search(node.right, symbol)

class User:
    USERS_FILE = "users.csv"

    def __init__(self):
        self.users = self.load_users()

    def load_users(self):
        if not os.path.exists(self.USERS_FILE):
            return {}
        with open(self.USERS_FILE, "r") as file:
            reader = csv.reader(file)
            return {rows[0]: rows[1] for rows in reader}

    def save_users(self):
        with open(self.USERS_FILE, "w", newline="") as file:
            writer = csv.writer(file)
            for username, password in self.users.items():
                writer.writerow([username, password])

    def register(self, username, password):
        if username in self.users:
            print("⚠ Username already exists!")
            return False
        self.users[username] = password
        self.save_users()
        print(f"✅ User '{username}' registered successfully!")
        return True

    def login(self, username, password):
        if username in self.users and self.users[username] == password:
            print(f"🔓 Login successful for {username}!")
            return True
        print("❌ Invalid username or password!")
        return False

class StockMarket:
    def __init__(self, username):
        self.username = username
        self.avl_tree = AVLTree()
        self.tracker = TransactionTracker()
        self.load_data()

    def get_stock_file(self):
        return f"{self.username}_stocks.csv"

    def get_transaction_file(self):
        return f"{self.username}_transactions.csv"

    def save_data(self):
        with open(self.get_stock_file(), "w", newline="") as file:
            writer = csv.writer(file)
            writer.writerow(["Symbol", "Price", "Quantity"])
            self._save_tree(self.avl_tree.root, writer)

        self.tracker.save_transactions_to_csv(self.get_transaction_file())

    def _save_tree(self, node, writer):
        if node:
            writer.writerow([node.stock.symbol, node.stock.price, node.stock.quantity])
            self._save_tree(node.left, writer)
            self._save_tree(node.right, writer)

    def load_data(self):
        if os.path.exists(self.get_stock_file()):
            with open(self.get_stock_file(), "r") as file:
                reader = csv.reader(file)
                next(reader)
                for row in reader:
                    symbol, price, quantity = row
                    self.avl_tree.insert(Stock(symbol, float(price), int(quantity)))

        self.tracker.load_transactions_from_csv(self.get_transaction_file())

    def buy_stock(self, symbol, price, quantity):
        stock = self.avl_tree.search(symbol)
        if stock:
            total_quantity = stock.quantity + quantity
            new_price = ((stock.price * stock.quantity) + (price * quantity)) / total_quantity
            stock.price = round(new_price, 2)
            stock.quantity = total_quantity
        else:
            stock = Stock(symbol, price, quantity)
            self.avl_tree.insert(stock)

        self.tracker.add_transaction(symbol, price, "BUY")
        self.save_data()
        print(f"✅ Bought {quantity} shares of {symbol} at ${price}")

    def sell_stock(self, symbol, quantity):
        stock = self.avl_tree.search(symbol)
        if stock and stock.quantity >= quantity:
            stock.quantity -= quantity
            self.tracker.add_transaction(symbol, stock.price, "SELL")
            self.save_data()
            print(f"✅ Sold {quantity} shares of {symbol}")
        else:
            print("⚠ Not enough shares to sell or stock not found!")

    def search_stock(self, symbol):
        stock = self.avl_tree.search(symbol)
        if stock:
            print(f"🔎 Found: {stock}")
        else:
            print("❌ Stock not found!")

    def view_transaction_history(self):
        print("\n📜Transaction History:")
        history = self.tracker.get_full_transaction_history()
        lowest = self.tracker.get_lowest_transaction()
        highest = self.tracker.get_highest_transaction()

        if not history:
            print("⚠ No transactions found!")
            return

        print("\nHistory:")
        for t in history:
            print(t)
        if lowest:
            print(f"\nLowest Transaction: {lowest}")
        if highest:
            print(f"Highest Transaction: {highest}")


def main():
    user_system = User()
    while True:
        print("\n📈 STOCK MARKET SYSTEM 📉")
        choice = input("1: Register | 2: Login | 3: Exit\nChoose: ")
        if choice == "1":
            username = input("Enter username: ")
            password = input("Enter password: ")
            user_system.register(username, password)
        elif choice == "2":
            username = input("Enter username: ")
            password = input("Enter password: ")
            if user_system.login(username, password):
                stock_market = StockMarket(username)
                while True:
                    action = input("\n1: Buy Stock | 2: Sell Stock | 3: Search Stock | 4: View Transactions | 5: Logout\nChoose: ")
                    if action == "1":
                        symbol = input("Enter stock symbol: ")
                        price = float(input("Enter buy price: "))
                        quantity = int(input("Enter quantity: "))
                        stock_market.buy_stock(symbol, price, quantity)
                    elif action == "2":
                        symbol = input("Enter stock symbol: ")
                        quantity = int(input("Enter quantity: "))
                        stock_market.sell_stock(symbol, quantity)
                    elif action == "3":
                        symbol = input("Enter stock symbol: ")
                        stock_market.search_stock(symbol)
                    elif action == "4":
                        stock_market.view_transaction_history()
                    elif action == "5":
                        break
        elif choice == "3":
            break

if __name__ == "__main__":
    main()
