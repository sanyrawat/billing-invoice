import { useState } from "react";

const API_BASE_URL = `${window.location.protocol}//${window.location.hostname}:8080`;

const createItem = () => ({
  description: "",
  hsnSac: "",
  quantity: 1,
  unit: "Pcs",
  unitPrice: 0
});

const initialForm = {
  invoiceNumber: "",
  invoiceDate: "2026-04-01",
  customerName: "JOINT DIRECTOR",
  customerAddress: "Office of Joint Director Of Education, Basti G.I.C Campus Gandhi Nagar, BASTI, Uttar Pradesh - 272001, India",
  customerContactNo: "05542284717",
  customerGstin: "09AAAJD0000A1Z5",
  taxPercentage: 0,
  companyName: "VAISHNO ENTERPRISES",
  companyAddress: "C-108A, Dayal Residency, Faizabad Road, Chinhut, Lucknow (226028)",
  companyPhone: "9696605886",
  companyEmail: "Vaishnoenterpris@gmail.com",
  companyGstin: "09BCOPR1632Q1ZS",
  companyState: "09-Uttar Pradesh",
  poDate: "2026-03-26",
  poNumber: "511687794906159",
  ewayBillNumber: "411704007408",
  bankName: "Union Bank Of India, Faizabad Road",
  accountNumber: "555001010050732",
  ifscCode: "UBIN0555509",
  accountHolderName: "VAISHNO ENTERPRISES",
  items: [
    {
      description: "ACER HIGH END DESKTOP COMPUTER WITH WARRANTY 3 Year",
      hsnSac: "8471",
      quantity: 1,
      unit: "Pcs",
      unitPrice: 64797
    }
  ]
};

function App() {
  const [formData, setFormData] = useState(initialForm);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const updateField = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const updateItem = (index, field, value) => {
    setFormData((prev) => {
      const items = [...prev.items];
      items[index] = { ...items[index], [field]: value };
      return { ...prev, items };
    });
  };

  const addItem = () => {
    setFormData((prev) => ({ ...prev, items: [...prev.items, createItem()] }));
  };

  const removeItem = (index) => {
    setFormData((prev) => {
      if (prev.items.length === 1) {
        return prev;
      }
      return { ...prev, items: prev.items.filter((_, idx) => idx !== index) };
    });
  };

  const sanitizePayload = () => ({
    ...formData,
    taxPercentage: Number(formData.taxPercentage),
    items: formData.items.map((item) => ({
      description: item.description,
      hsnSac: item.hsnSac,
      quantity: Number(item.quantity),
      unit: item.unit,
      unitPrice: Number(item.unitPrice)
    }))
  });

  const clearCustomerAndItems = () => {
    setFormData((prev) => ({
      ...prev,
      invoiceNumber:"VE26",
      customerName: "",
      customerAddress: "",
      customerContactNo: "",
      customerGstin: "",
      ewayBillNumber:"0",
      poNumber:"GEMC-5116877",
      poDate:"",
      items: [createItem()]
    }));
  };

  const generateInvoice = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const payload = sanitizePayload();
      const response = await fetch(`${API_BASE_URL}/api/invoices/generate`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const responseText = await response.text();
        throw new Error(responseText || "Unable to generate invoice PDF");
      }

      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `${formData.invoiceNumber || "download"}.pdf`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="card">
        <h1>Invoice PDF Generator</h1>
        <p>Fill the form and click generate to download invoice PDF.</p>

        <form onSubmit={generateInvoice}>
          <section>
            <h2>Company Details</h2>
            <div className="grid">
              <input value={formData.companyName} onChange={(e) => updateField("companyName", e.target.value)} placeholder="Company Name" required />
              <input value={formData.companyAddress} onChange={(e) => updateField("companyAddress", e.target.value)} placeholder="Company Address" required />
              <input value={formData.companyPhone} onChange={(e) => updateField("companyPhone", e.target.value)} placeholder="Company Phone" />
              <input value={formData.companyEmail} onChange={(e) => updateField("companyEmail", e.target.value)} placeholder="Company Email" />
              <input value={formData.companyGstin} onChange={(e) => updateField("companyGstin", e.target.value)} placeholder="GSTIN" />
              <input value={formData.companyState} onChange={(e) => updateField("companyState", e.target.value)} placeholder="State" />
            </div>
          </section>

          <section>
            <h2>Invoice Details</h2>
            <div className="grid">
              <input value={formData.invoiceNumber} onChange={(e) => updateField("invoiceNumber", e.target.value)} placeholder="Invoice Number" required />
              <input type="date" value={formData.invoiceDate} onChange={(e) => updateField("invoiceDate", e.target.value)} required />
              <input type="date" value={formData.poDate} onChange={(e) => updateField("poDate", e.target.value)} />
              <input value={formData.poNumber} onChange={(e) => updateField("poNumber", e.target.value)} placeholder="PO Number" />
              <input value={formData.ewayBillNumber} onChange={(e) => updateField("ewayBillNumber", e.target.value)} placeholder="E-way Bill Number" />
              <input type="number" min="0" step="0.01" value={formData.taxPercentage} onChange={(e) => updateField("taxPercentage", e.target.value)} placeholder="Tax Percentage" />
            </div>
          </section>

          <section>
            <h2>Customer Details</h2>
            <div className="grid">
              <input value={formData.customerName} onChange={(e) => updateField("customerName", e.target.value)} placeholder="Customer Name" required />
              <input value={formData.customerAddress} onChange={(e) => updateField("customerAddress", e.target.value)} placeholder="Customer Address" required />
              <input value={formData.customerContactNo} onChange={(e) => updateField("customerContactNo", e.target.value)} placeholder="Customer Contact Number" />
              <input value={formData.customerGstin} onChange={(e) => updateField("customerGstin", e.target.value)} placeholder="Customer GST Number" />
            </div>
          </section>

          <section>
            <h2>Line Items</h2>
            {formData.items.map((item, idx) => (
              <div className="item-row" key={idx}>
                <input
                  value={item.description}
                  onChange={(e) => updateItem(idx, "description", e.target.value)}
                  placeholder="Description"
                  required
                />
                <input
                  value={item.hsnSac}
                  onChange={(e) => updateItem(idx, "hsnSac", e.target.value)}
                  placeholder="HSN/SAC"
                />
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={item.quantity}
                  onChange={(e) => updateItem(idx, "quantity", e.target.value)}
                  placeholder="Quantity"
                  required
                />
                <input
                  value={item.unit}
                  onChange={(e) => updateItem(idx, "unit", e.target.value)}
                  placeholder="Unit"
                />
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={item.unitPrice}
                  onChange={(e) => updateItem(idx, "unitPrice", e.target.value)}
                  placeholder="Unit Price"
                  required
                />
                <button type="button" className="danger" onClick={() => removeItem(idx)}>
                  Remove
                </button>
              </div>
            ))}
            <div className="button-row">
              <button type="button" onClick={addItem}>+ Add Item</button>
              <button type="button" className="warn" onClick={clearCustomerAndItems}>Clear Customer & Items</button>
            </div>
          </section>

          <section>
            <h2>Bank Details</h2>
            <div className="grid">
              <input value={formData.bankName} onChange={(e) => updateField("bankName", e.target.value)} placeholder="Bank Name" />
              <input value={formData.accountNumber} onChange={(e) => updateField("accountNumber", e.target.value)} placeholder="Account Number" />
              <input value={formData.ifscCode} onChange={(e) => updateField("ifscCode", e.target.value)} placeholder="IFSC Code" />
              <input value={formData.accountHolderName} onChange={(e) => updateField("accountHolderName", e.target.value)} placeholder="Account Holder Name" />
            </div>
          </section>

          {error ? <pre className="error">{error}</pre> : null}
          <button className="primary" type="submit" disabled={loading}>
            {loading ? "Generating..." : "Generate Invoice PDF"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default App;
