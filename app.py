import streamlit as st
from stock_system import User, StockMarket

# --- Session State for Login Persistence ---
if 'logged_in' not in st.session_state:
    st.session_state.logged_in = False
    st.session_state.username = ""

# --- Login/Register Interface ---
def login_register_ui():
    user_system = User()
    st.title("📈 Stock Market Management")
    
    tab1, tab2 = st.tabs(["Login", "Register"])

    with tab1:
        st.subheader("🔓 Login")
        username = st.text_input("Username", key="login_user")
        password = st.text_input("Password", type="password", key="login_pass")
        if st.button("Login"):
            if user_system.login(username, password):
                st.session_state.logged_in = True
                st.session_state.username = username
                st.experimental_rerun()
            else:
                st.error("Invalid credentials!")

    with tab2:
        st.subheader("📝 Register")
        new_username = st.text_input("New Username", key="reg_user")
        new_password = st.text_input("New Password", type="password", key="reg_pass")
        if st.button("Register"):
            if user_system.register(new_username, new_password):
                st.success("User registered successfully! Please log in.")
            else:
                st.warning("Username already exists.")

# --- Dashboard After Login ---
def dashboard():
    st.title(f"📊 Welcome, {st.session_state.username}!")
    stock_market = StockMarket(st.session_state.username)

    action = st.selectbox("Choose an action", ["Buy Stock", "Sell Stock", "Search Stock", "Transaction History"])

    if action == "Buy Stock":
        st.subheader("🛒 Buy Stock")
        symbol = st.text_input("Stock Symbol")
        price = st.number_input("Buy Price", min_value=0.0)
        quantity = st.number_input("Quantity", min_value=1, step=1)
        if st.button("Buy"):
            stock_market.buy_stock(symbol, price, quantity)
            st.success(f"Bought {quantity} shares of {symbol} at ${price}")

    elif action == "Sell Stock":
        st.subheader("💰 Sell Stock")
        symbol = st.text_input("Stock Symbol to Sell")
        quantity = st.number_input("Quantity to Sell", min_value=1, step=1)
        if st.button("Sell"):
            stock_market.sell_stock(symbol, quantity)

    elif action == "Search Stock":
        st.subheader("🔎 Search Stock")
        symbol = st.text_input("Stock Symbol to Search")
        if st.button("Search"):
            stock = stock_market.avl_tree.search(symbol)
            if stock:
                st.info(f"{stock}")
            else:
                st.warning("Stock not found.")

    elif action == "Transaction History":
        st.subheader("📜 Transaction History")
        transactions = stock_market.tracker.get_full_transaction_history()
        lowest = stock_market.tracker.get_lowest_transaction()
        highest = stock_market.tracker.get_highest_transaction()

        if transactions:
            st.write("### All Transactions:")
            st.table(transactions)
        else:
            st.warning("No transactions yet.")

        if lowest:
            st.success(f"Lowest Transaction: {lowest}")
        if highest:
            st.success(f"Highest Transaction: {highest}")

    st.button("🔒 Logout", on_click=lambda: st.session_state.update({"logged_in": False, "username": ""}))

# --- Main App ---
if st.session_state.logged_in:
    dashboard()
else:
    login_register_ui()
