#!/usr/bin/env python3
"""
DCI RMT Validation Script
Helps validate RMT DCI results and compare with MCI data
"""

import pandas as pd
import numpy as np
import sys
import os

def load_dci_results(csv_file):
    """Load RMT DCI results from CSV file"""
    try:
        df = pd.read_csv(csv_file)
        print(f"✓ Loaded DCI results from {csv_file}")
        print(f"  Services analyzed: {len(df)}")
        print(f"  Average DCI score: {df['DCI'].mean():.3f}")
        return df
    except Exception as e:
        print(f"✗ Error loading {csv_file}: {e}")
        return None

def analyze_dci_results(df):
    """Analyze RMT DCI results"""
    print("\n=== RMT DCI Analysis ===")
    
    # Status distribution
    status_counts = df['Status'].value_counts()
    print("Status Distribution:")
    for status, count in status_counts.items():
        print(f"  {status}: {count} services")
    
    # Score statistics
    print(f"\nScore Statistics:")
    print(f"  Min: {df['DCI'].min():.3f}")
    print(f"  Max: {df['DCI'].max():.3f}")
    print(f"  Mean: {df['DCI'].mean():.3f}")
    print(f"  Std: {df['DCI'].std():.3f}")
    
    # High coupling services
    high_coupling = df[df['DCI'] >= 0.7]
    if len(high_coupling) > 0:
        print(f"\nHigh Coupling Services ({len(high_coupling)}):")
        for _, row in high_coupling.iterrows():
            print(f"  {row['Service']}: {row['DCI']:.3f}")
    
    # Isolated services
    isolated = df[df['DCI'] == 0.0]
    if len(isolated) > 0:
        print(f"\nIsolated Services ({len(isolated)}):")
        for _, row in isolated.iterrows():
            print(f"  {row['Service']}")

def compare_with_mci(dci_df, mci_data_file=None):
    """Compare DCI results with MCI data"""
    print("\n=== MCI Comparison ===")
    
    if mci_data_file and os.path.exists(mci_data_file):
        try:
            mci_df = pd.read_csv(mci_data_file)
            print(f"✓ Loaded MCI data from {mci_data_file}")
            
            # Merge data on service names
            merged = pd.merge(dci_df, mci_df, on='Service', how='inner')
            
            if len(merged) > 0:
                # Calculate correlations
                correlation = merged['DCI'].corr(merged['MCI_Afferent'])
                print(f"Correlation (DCI vs MCI_Afferent): {correlation:.3f}")
                
                # Identify patterns
                merged['Difference'] = abs(merged['DCI'] - merged['MCI_Afferent'])
                high_diff = merged[merged['Difference'] > 0.3]
                
                print(f"Services with significant differences (>0.3): {len(high_diff)}")
                if len(high_diff) > 0:
                    print("Services to investigate:")
                    for _, row in high_diff.iterrows():
                        print(f"  {row['Service']}: DCI={row['DCI']:.3f}, MCI={row['MCI_Afferent']:.3f}")
                
                return merged
            else:
                print("No matching services found between DCI and MCI data")
                
        except Exception as e:
            print(f"✗ Error comparing with MCI: {e}")
    else:
        print("No MCI data file provided or file not found")
        print("To compare with MCI, create a CSV file with columns: Service,MCI_Afferent,MCI_Efferent")
    
    return None

def create_comparison_template(dci_df):
    """Create a template for manual MCI comparison"""
    template_file = "mci_comparison_template.csv"
    
    template_df = dci_df[['Service', 'DCI']].copy()
    template_df['MCI_Afferent'] = ''
    template_df['MCI_Efferent'] = ''
    template_df['Notes'] = ''
    
    template_df.to_csv(template_file, index=False)
    print(f"\n✓ Created MCI comparison template: {template_file}")
    print("  Fill in the MCI values from the research paper and run this script again")

def main():
    """Main validation function"""
    print("DCI RMT Validation Script")
    print("=" * 40)
    
    # Check command line arguments
    if len(sys.argv) < 2:
        print("Usage: python validate_rmt_dci.py <dci_results.csv> [mci_data.csv]")
        print("\nExample:")
        print("  python validate_rmt_dci.py dci_output_rmt.csv")
        print("  python validate_rmt_dci.py dci_output_rmt.csv mci_data.csv")
        return
    
    dci_file = sys.argv[1]
    mci_file = sys.argv[2] if len(sys.argv) > 2 else None
    
    # Load and analyze DCI results
    dci_df = load_dci_results(dci_file)
    if dci_df is None:
        return
    
    analyze_dci_results(dci_df)
    
    # Compare with MCI if available
    if mci_file:
        compare_with_mci(dci_df, mci_file)
    else:
        create_comparison_template(dci_df)
        print("\nTo complete validation:")
        print("1. Get MCI data from the research paper")
        print("2. Fill in the template with MCI values")
        print("3. Run: python validate_rmt_dci.py dci_output_rmt.csv mci_data.csv")

if __name__ == "__main__":
    main() 